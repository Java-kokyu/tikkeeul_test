package com.hanghae0705.sbmoney.security.filter;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException
    {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        String origin = request.getHeader("Origin");

        if (origin == null){
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        } else if(origin.startsWith("http://localhost:3000")
                || origin.startsWith("http://sparta-ej.shop")
                || origin.startsWith("https://www.tikkeeul.com")){
            response.setHeader("Access-Control-Allow-Origin", origin); //허용대상 도메인
        }
        //response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        //response.setHeader("Access-Control-Allow-Origin", "https://accounts.google.com");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT, PATCH, OPTIONS");
        response.setHeader("Access-Control-Max-Age", "3600");
        //response.setHeader("Access-Control-Allow-Headers", "X-Requested-With, Origin, Content-Type, Accept, Custom-Header, Authorization");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");


        chain.doFilter(req, res);
    }
    public void init(FilterConfig filterConfig) {}
    public void destroy() {}
}