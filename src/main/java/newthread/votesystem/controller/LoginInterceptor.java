package newthread.votesystem.controller;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
            throws Exception {
        // TODO Auto-generated method  stub

    }

    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
            throws Exception {

    }

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object arg2) throws Exception {


        HttpSession session = httpServletRequest.getSession();
        String homeUrl = httpServletRequest.getContextPath();

        System.out.println(homeUrl);
        if (session.getAttribute("userName") == null) {

            // 如果是 ajax 请求，则设置 session 状态 、CONTEXTPATH 的路径值
            // 如果是ajax请求响应头会有，x-requested-with
            if (httpServletRequest.getHeader("x-requested-with") != null && httpServletRequest.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")) {
                httpServletResponse.setHeader("sessionstatus", "TIMEOUT");

                //部署到nginx时要删掉 homeUrl
                System.out.println("Ajax，拦截");
                httpServletResponse.setHeader("CONTEXTPATH", homeUrl + "/index.html");
                // FORBIDDEN，forbidden。也就是禁止、403
                httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            } else {
                // 如果不是 ajax 请求，则直接跳转即可
                System.out.println("非Ajax，直接跳转");
                httpServletResponse.sendRedirect(homeUrl + "/index.html");

            }
            return false;
        }
        return true;
    }


    /**
     *对于请求是ajax请求重定向问题的处理方法
     */
    public void redirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //获取当前请求的路径
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

        //如果request.getHeader("X-Requested-With") 返回的是"XMLHttpRequest"说明就是ajax请求，
        // 需要特殊处理 否则直接重定向就可以了
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            //告诉ajax我是重定向
            response.setHeader("REDIRECT", "REDIRECT");
            //告诉ajax我重定向的路径,部署到nginx时要删掉 homeUrl
            response.setHeader("CONTENTPATH", basePath + "/index.html");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            response.sendRedirect(basePath + "/index.html");
        }
    }
}
