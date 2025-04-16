package Se2.MovieTicket.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // Lấy thông tin lỗi
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        // Thêm thông tin lỗi vào model để hiển thị trong template
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            model.addAttribute("status", statusCode);

            // Xử lý các loại lỗi cụ thể
            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "403"; // Trang lỗi 403 tùy chỉnh
            } else if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "404"; // Trang lỗi 404 tùy chỉnh
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "500"; // Trang lỗi 500 tùy chỉnh
            }
        }

        if (message != null && !message.toString().isEmpty()) {
            model.addAttribute("message", message.toString());
        } else {
            model.addAttribute("message", "Không có thông báo lỗi chi tiết");
        }

        // Trả về template lỗi chung
        return "error";
    }
}