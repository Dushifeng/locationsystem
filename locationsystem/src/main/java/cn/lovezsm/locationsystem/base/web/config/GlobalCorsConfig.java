package cn.lovezsm.locationsystem.base.web.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Configuration
public class GlobalCorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
                /*
                 * 1、需要先定义一个convert转换消息的对象 2、添加fastJson的配置信息，比如：是否要格式化返回json数据 3、在convert中添加配置信息
                 * 4、将convert添加到converters当中
                 *
                 */
                // 1、需要先定义一个·convert转换消息的对象；
                Iterator<HttpMessageConverter<?>> iterator = converters.iterator();
                while(iterator.hasNext()){
                    HttpMessageConverter<?> converter = iterator.next();
                    if(converter instanceof MappingJackson2HttpMessageConverter){
                        iterator.remove();
                    }
                }
                FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
                //自定义配置...
                FastJsonConfig config = new FastJsonConfig();
                config.setSerializerFeatures(SerializerFeature.QuoteFieldNames,
                        SerializerFeature.WriteEnumUsingToString,
                        /*SerializerFeature.WriteMapNullValue,*/
                        SerializerFeature.WriteDateUseDateFormat,
                        SerializerFeature.DisableCircularReferenceDetect);
                fastJsonHttpMessageConverter.setFastJsonConfig(config);


                List<MediaType> supportedMediaTypes = new ArrayList<>();
                supportedMediaTypes.add(MediaType.APPLICATION_JSON);
                supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
                supportedMediaTypes.add(MediaType.APPLICATION_ATOM_XML);
                supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
                supportedMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
                supportedMediaTypes.add(MediaType.APPLICATION_PDF);
                supportedMediaTypes.add(MediaType.APPLICATION_RSS_XML);
                supportedMediaTypes.add(MediaType.APPLICATION_XHTML_XML);
                supportedMediaTypes.add(MediaType.APPLICATION_XML);
                supportedMediaTypes.add(MediaType.IMAGE_GIF);
                supportedMediaTypes.add(MediaType.IMAGE_JPEG);
                supportedMediaTypes.add(MediaType.IMAGE_PNG);
                supportedMediaTypes.add(MediaType.TEXT_EVENT_STREAM);
                supportedMediaTypes.add(MediaType.TEXT_HTML);
                supportedMediaTypes.add(MediaType.TEXT_MARKDOWN);
                supportedMediaTypes.add(MediaType.TEXT_PLAIN);
                supportedMediaTypes.add(MediaType.TEXT_XML);
                fastJsonHttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
                converters.add(fastJsonHttpMessageConverter);
                System.out.println(converters);
            }

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                //添加映射路径
                registry.addMapping("/**")
                        //放行哪些原始域
                        .allowedOrigins("*")
                        //是否发送Cookie信息
                        .allowCredentials(true)
                        //放行哪些原始域(请求方式)
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        //放行哪些原始域(头部信息)
                        .allowedHeaders("*")
                        //暴露哪些头部信息（因为跨域访问默认不能获取全部头部信息）
                        .exposedHeaders("Header1", "Header2");
            }
        };
    }

}
