package com.nmm.study;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;

/**
 * @author nmm 2018/6/22
 * @description
 */
@SpringBootApplication
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class,args);
    }

    /**
     * 重写EmbeddedServletContainerFacotry，在web容器中添加对于https的支持
     * @return
     */
    @Bean
    public EmbeddedServletContainerFactory servletContainerFactory(){
//        JettyEmbeddedServletContainerFactory
        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory(){
            //重写请求处理
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint constraint = new SecurityConstraint();
//                INTEGRAL, or CONFIDENTIAL. tomcatapi用于配置访问时加密的，也就是支持https
                constraint.setUserConstraint("CONFIDENTIAL");
                //安全集合
                SecurityCollection collection = new SecurityCollection();
                //配置https的适配url
                collection.addPattern("/login");
                constraint.addCollection(collection);
                context.addConstraint(constraint);
            }
        };
        //添加额外连接支持
        tomcat.addAdditionalTomcatConnectors(httpConnector());
        return tomcat;
    }
    @Bean
    public Connector httpConnector(){
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        //connector监听的端口号
        connector.setPort(8080);
        connector.setSecure(false);
        //监听到http的端口后转向到https的端口
        connector.setRedirectPort(8443);
        return connector;
    }
}
