package com.imooc.zuul.consistent;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

/**
 * zuul中请求的request.getInputStream()，只能读取一次
 * 在分析请求内容后，会关闭stream流，后续无法获取。
 */
public class BodyReaderHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private byte[] body;

    public BodyReaderHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        try {
            body = StreamUtils.getByteByStream(request.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }

            @Override
            public int read() {
                return bais.read();
            }
        };
    }
}