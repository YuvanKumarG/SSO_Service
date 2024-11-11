package com.sso.app.loggerfilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class RequestLoggingFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String uuid = UUID.randomUUID().toString();

		String requestUUID = Optional.ofNullable(httpServletRequest).map(req -> req.getHeader("requestUUID"))
				.filter(StringUtils::isNotBlank).orElse(uuid);

		MDC.put("requestUUID", requestUUID);

		chain.doFilter(request, response);

		MDC.clear();
	}
}
