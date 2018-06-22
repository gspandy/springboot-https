# springboot-https
springboot对https的支持
## 1、生成安全证书
  keytool -genkey -alias springboot -keypass 123456 -keyalg RSA -keysize 1024 -validity 3650 -keystore E:/tmp/springboottest.keystore -storepass 123456
## 2、修改配置文件
```
#配置监听端口
server:
  port: 8443
  ssl:
    key-store: target/classes/springboottest.keystore
    #密码要与keypaas一致。
    key-password: 123456
    key-store-type: JKS
    key-alias: springboot

```
## 3、修改EmbeddedServletContainerFactory，添加对于加密的支持，同时增加额外的http连接支持
这里我们只对/login应用加密，通过http访问/login时，会自动跳转到https下
```
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
```
## 4、说明
这里我们让springboot默认为https协议，因此所有的请求都可以通过https访问，而且我们配置了额外的8080http连接，所有请求也可以通过http访问，但是在EmbeddedServletContainerFactory中，我们
添加了某些url样式需要走加密协议，这部分url，在通过http访问时就会自动跳到https下。
基于此，我们这类https服务，需要考虑的是，只有必要的https服务才放入这里，因为https在效率上还是略低的。
