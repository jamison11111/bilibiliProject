package com.lwc.config;

import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 跨域解决配置
 *
 * 跨域概念：
 *      出于浏览器的同源策略限制，同源策略会阻止一个域的javascript脚本和另外一个域的内容进行交互。
 *      所谓同源就是指两个页面具有相同的协议（protocol），主机（host）和端口号（port）
 *
 * 非同源的限制：
 *  【1】无法读取非同源网页的 Cookie、LocalStorage 和 IndexedDB
 *  【2】无法接触非同源网页的 DOM
 *  【3】可以向非同源地址发送 AJAX 请求,但无法获取请求的响应数据
 *
 *  spingboot解决跨域方案：CORS 是跨域资源分享（Cross-Origin Resource Sharing）的缩写。
 *  它是 W3C 标准，属于跨源 AJAX 请求的根本解决方法。
 *
 *
 *  Filter是用来过滤(拦截)任务的，既可以被使用在请求资源，也可以是资源响应，或者二者都有
 *  Filter使用doFilter方法进行过滤(拦截)
 */
/*解决的问题:浏览器页面向本网关服务发起一个跨域的Ajax请求，本服务返回一些数据给浏览器，但浏览器发现这些数据是来自跨域服务器的数据,由于浏览器的同源策略限制，
它就不会将这些返回的数据返回给浏览器前端,这个跨域问题的解决方案之一就是在服务器端配置Cors,做一些设置,告知浏览器,我允许与我不同域的服务器页面获取我返回的这份数据,
所以你就大胆的把数据交付过去吧*/


/*过滤器的实现类会拦截输入本服务的请求进行一些校验设置
事实上,本网关项目若只是用postman进行接口测试的话,完全不用这个跨域配置文件也不会出问题，因为没有用到浏览器。
什么时候会有问题?在浏览器页面上的一个非同源网址向本网关服务发起ajax请求,并试图获取ajax请求返回的数据时,会获取不到这个返回的数据
这时候才会遇到所谓跨域问题。比如我在百度浏览器页面上点击一个图标，然后触发了javascript事件，浏览器在百度页面上向本服务器发起了一个形如
localhost:15008/bilibili-api/rsa-pks的请求,本网关服务器将此请求转发到localhost:8080/rsa-pks,获取了rsa密钥数据，
将其返回给浏览器上的"百度服务器ip:百度端口",浏览器发现当前页面网址和网关网址不同源，于是拒绝把ajax请求的响应数据(也就是rsa密钥数据)交互给
浏览器上的百度页面,这里的解决方案是在网关服务器上对响应头做一些设置(主要是设置一些源白名单)，告知浏览器，这一份响应数据可以交付给前端界面(若前端界面源是这些白名单内的Ip和端口的话),白名单内的,不同源也没关系
所以这个跨域配置在实际项目中才有用，在这个项目练习里,只用postman测试本项目的接口，这个跨域配置类是没用的。*/
@Configuration
public class CorsConfig implements Filter {

//    private final String[] allowedDomain = {"http://localhost:8080", "http://39.107.54.180"};
    //不写端口号的话,浏览器会自动将其识别为80端口,跨域源白名单
    private final String[] allowedDomain = {"http://localhost:8080", "http://192.168.3.201"};


    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        //设置允许跨域的ip-端口列表
        Set<String> allowedOrigins= new HashSet<>(Arrays.asList(allowedDomain));
        //获取跨域请求的来源
        String origin=httpRequest.getHeader("Origin");
        if (origin == null) {
            //放行
            chain.doFilter(request, response);
            return;
        }
        //设置一些响应头,目的是告知浏览器,放行跨域请求源获取本服务器的数据
        if (allowedOrigins.contains(origin)){
            httpResponse.setHeader("Access-Control-Allow-Origin", origin);
            httpResponse.setContentType("application/json;charset=UTF-8");
            httpResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
            httpResponse.setHeader("Access-Control-Max-Age", "3600");
            httpResponse.setHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With, userId, token, ut");//表明服务器支持的所有头信息字段
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true"); //如果要让Cookie也能返回回去，需要指定Access-Control-Allow-Credentials字段为true;
            httpResponse.setHeader("XDomainRequestAllowed","1");
        }
        //放行,如果不设置就放行,那目标浏览器还是得不到本服务器的数据
        chain.doFilter(request, response);
    }
}
