package admin.controller;

import dal.VoucherDAO;
import model.Voucher;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet quản lý vouchers cho admin
 */
@WebServlet(name = "AdminVoucherServlet", urlPatterns = {"/admin/vouchers"})
public class AdminVoucherServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        VoucherDAO voucherDAO = new VoucherDAO();
        List<Voucher> voucherList = voucherDAO.getAllVouchers();
        
        // Load voucher để edit nếu có voucher_id
        String voucherIdStr = request.getParameter("voucher_id");
        if (voucherIdStr != null && !voucherIdStr.isEmpty() && request.getParameter("action") == null) {
            try {
                Voucher editVoucher = voucherDAO.getVoucherById(Integer.parseInt(voucherIdStr));
                request.setAttribute("editVoucher", editVoucher);
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
        
        request.setAttribute("voucherList", voucherList);
        request.setAttribute("activePage", "vouchers");
        request.getRequestDispatcher("/admin/vouchers.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        
        String action = request.getParameter("action");
        VoucherDAO voucherDAO = new VoucherDAO();
        
        if ("create".equals(action)) {
            try {
                Voucher voucher = new Voucher();
                voucher.setCode(request.getParameter("code"));
                voucher.setDiscount_type(request.getParameter("discount_type"));
                voucher.setDiscount_value(Double.parseDouble(request.getParameter("discount_value")));
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                voucher.setStart_date(new Timestamp(sdf.parse(request.getParameter("start_date")).getTime()));
                voucher.setEnd_date(new Timestamp(sdf.parse(request.getParameter("end_date")).getTime()));
                
                voucher.setDescription(request.getParameter("description"));
                voucher.setIs_active("1".equals(request.getParameter("is_active")));
                
                voucherDAO.createVoucher(voucher);
                response.sendRedirect("vouchers?success=created");
            } catch (Exception e) {
                response.sendRedirect("vouchers?error=" + e.getMessage());
            }
        } else if ("update".equals(action)) {
            try {
                Voucher voucher = new Voucher();
                voucher.setVoucher_id(Integer.parseInt(request.getParameter("voucher_id")));
                voucher.setCode(request.getParameter("code"));
                voucher.setDiscount_type(request.getParameter("discount_type"));
                voucher.setDiscount_value(Double.parseDouble(request.getParameter("discount_value")));
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                voucher.setStart_date(new Timestamp(sdf.parse(request.getParameter("start_date")).getTime()));
                voucher.setEnd_date(new Timestamp(sdf.parse(request.getParameter("end_date")).getTime()));
                
                voucher.setDescription(request.getParameter("description"));
                voucher.setIs_active("1".equals(request.getParameter("is_active")));
                
                voucherDAO.updateVoucher(voucher);
                response.sendRedirect("vouchers?success=updated");
            } catch (Exception e) {
                response.sendRedirect("vouchers?error=" + e.getMessage());
            }
        } else if ("delete".equals(action)) {
            try {
                int voucherId = Integer.parseInt(request.getParameter("voucher_id"));
                voucherDAO.deleteVoucher(voucherId);
                response.sendRedirect("vouchers?success=deleted");
            } catch (Exception e) {
                response.sendRedirect("vouchers?error=" + e.getMessage());
            }
        } else {
            response.sendRedirect("vouchers");
        }
    }
}


