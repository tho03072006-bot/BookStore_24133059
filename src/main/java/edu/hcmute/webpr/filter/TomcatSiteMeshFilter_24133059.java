package edu.hcmute.webpr.filter;

import java.io.IOException;
import java.io.Writer;

import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.sitemesh.config.ConfigurableSiteMeshFilter;
import org.sitemesh.config.properties.PropertiesFilterConfigurator;
import org.sitemesh.config.xml.XmlFilterConfigurator;
import org.sitemesh.content.Content;
import org.sitemesh.webapp.SiteMeshFilter;
import org.sitemesh.webapp.WebAppContext;
import org.sitemesh.webapp.contentfilter.ResponseMetaData;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * CÂU 1 - Filter của SiteMesh 3, có chỉnh lại cho chạy đúng trên Tomcat 11.
 *
 * <p><b>Vấn đề gặp phải.</b> SiteMesh 3.2.1 dựng trang decorator bằng
 * {@code RequestDispatcher.forward()}. Trên Tomcat 10/11, sau khi Servlet đã
 * forward sang file JSP nội dung thì response coi như đã gửi xong, nên cú
 * forward thứ hai (sang decorator) ném ngay
 * {@code IllegalStateException: Cannot forward after response has been
 * committed} &rarr; toàn bộ trang báo lỗi HTTP 500.</p>
 *
 * <p><b>Cách xử lý.</b> Lớp này ghi đè đúng một việc: đổi cách dựng decorator
 * sang {@code include()} và tự hứng nội dung decorator bằng
 * {@link DecoratorCaptureResponse_24133059} rồi trả lại cho SiteMesh ghép với
 * nội dung trang. Nhờ tự hứng nên không phụ thuộc vào việc container có đóng
 * writer hay không - dùng {@code include()} thì Tomcat KHÔNG đóng writer, nếu
 * để SiteMesh tự hứng thì nội dung còn kẹt trong bộ đệm và trang ra rỗng
 * (HTTP 200 nhưng dài 0 byte).</p>
 *
 * <p>Mọi cấu hình ánh xạ decorator vẫn đọc từ {@code WEB-INF/sitemesh3.xml}
 * như bình thường.</p>
 *
 * Đề số 02 - Trần Minh Thọ - 24133059
 */
public class TomcatSiteMeshFilter_24133059 extends ConfigurableSiteMeshFilter {

    private FilterConfig config;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.config = filterConfig;
        super.init(filterConfig);
    }

    @Override
    protected Filter setup() throws ServletException {
        SiteMeshFilterBuilder builder = new SiteMeshFilterBuilder() {
            @Override
            public Filter create() {
                boolean includeErrors = isIncludeErrorPages();
                return new SiteMeshFilter(getSelector(), getContentProcessor(),
                        getDecoratorSelector(), includeErrors) {
                    @Override
                    protected WebAppContext createContext(String type, HttpServletRequest req,
                            HttpServletResponse resp, ResponseMetaData metadata) {
                        return new WebAppContext(type, req, resp, config.getServletContext(),
                                getContentProcessor(), metadata, includeErrors) {
                            @Override
                            protected void decorate(String decoratorPath, Content content,
                                    Writer out) throws IOException {
                                renderDecorator(this, decoratorPath, content, out);
                            }
                        };
                    }
                };
            }
        };
        new PropertiesFilterConfigurator(getObjectFactory(), getConfigProperties(config))
                .configureFilter(builder);
        new XmlFilterConfigurator(getObjectFactory(), loadConfigXml(config, getConfigFileName()))
                .configureFilter(builder);
        applyCustomConfiguration(builder);
        return builder.create();
    }

    /**
     * Chạy file decorator và ghi kết quả vào {@code out} để SiteMesh ghép với
     * nội dung trang.
     */
    private static void renderDecorator(WebAppContext context, String decoratorPath,
            Content content, Writer out) throws IOException {

        HttpServletRequest request = context.getRequest();

        // Trang decorator đọc nội dung trang gốc qua 2 attribute này.
        Object oldContent = request.getAttribute(WebAppContext.CONTENT_KEY);
        Object oldContext = request.getAttribute(WebAppContext.CONTEXT_KEY);
        request.setAttribute(WebAppContext.CONTENT_KEY, content);
        request.setAttribute(WebAppContext.CONTEXT_KEY, context);

        try {
            RequestDispatcher dispatcher =
                    context.getServletContext().getRequestDispatcher(decoratorPath);
            if (dispatcher == null) {
                throw new IOException("Không tìm thấy decorator: " + decoratorPath);
            }

            DecoratorCaptureResponse_24133059 capture =
                    new DecoratorCaptureResponse_24133059(context.getResponse());
            dispatcher.include(request, capture);
            out.write(capture.getCapturedText());

        } catch (ServletException e) {
            throw new IOException("Lỗi khi dựng decorator " + decoratorPath, e);
        } finally {
            request.setAttribute(WebAppContext.CONTENT_KEY, oldContent);
            request.setAttribute(WebAppContext.CONTEXT_KEY, oldContext);
        }
    }
}
