package io.vusion.vtransmit.v2.commons.filter;

import java.io.IOException;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Component
public class Slf4jMDCFilter extends OncePerRequestFilter {
	
	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain chain) throws IOException, ServletException {
		
		try {
			
			chain.doFilter(request, response);
		} finally {
			MDC.clear();
		}
	}
	
	@Override
	protected boolean isAsyncDispatch(final HttpServletRequest request) {
		return false;
	}
	
	@Override
	protected boolean shouldNotFilterErrorDispatch() {
		return false;
	}
}
