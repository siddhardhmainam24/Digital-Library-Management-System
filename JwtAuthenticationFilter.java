package learning.outcome.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import learning.outcome.Entity.appUser;
import learning.outcome.Service.UserService;
import learning.outcome.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private Jwtutil jwtutil;
    @Autowired
    private UserService userService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


                String requestHeader = request.getHeader("Authorization");
                try{

        if(requestHeader == null && requestHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        if(requestHeader != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = requestHeader.split("Bearer ")[1];
            Long userId=jwtutil.extractUserId(token);
            appUser user=userService.getuserbyid(userId);
            List<SimpleGrantedAuthority>authorities= List.of(new SimpleGrantedAuthority("ROLE_"+user.getUserrole()));
            UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(user,authorities,null);
            authenticationToken.setDetails(new WebAuthenticationDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);


        }
        filterChain.doFilter(request, response);}
                catch(Exception e){
                    ApiResponse.error(e.getMessage());
                }





    }
}
