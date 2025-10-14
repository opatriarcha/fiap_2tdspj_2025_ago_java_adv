package br.com.fiap.tds._tdsq.Library.presentation.controllers;

import br.com.fiap.tds._tdsq.Library.infrastructure.config.JwtHelper;
import br.com.fiap.tds._tdsq.Library.presentation.controllers.transferObjects.AuthRequest;
import br.com.fiap.tds._tdsq.Library.presentation.controllers.transferObjects.AuthResponse;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtHelper jwtHelper;
    @Autowired private UserDetailsService userDetailsService;

//        curl -X POST http://localhost:8080/auth \
//                -H "Content-Type: application/json" \
//                -d '{"username": "user@gmail.com", "password": "0123456789"}'

    @PostMapping
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtHelper.generateToken(userDetails);
        String refreshToken = jwtHelper.generateRefreshToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }


    //    curl -X POST http://localhost:8080/auth/refresh \
    //            -H "Content-Type: application/json" \
    //            -d '{"refreshToken": "asfewbçcbçqbwerçovbaçuorebar"}'

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody TokenRefreshRequest request){
        String refreshToken = request.refreshToken();
        String username = jwtHelper.extractUsername(refreshToken);

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

        if( jwtHelper.isTokenValid(refreshToken, userDetails) ){
            String newAccessToken = jwtHelper.generateToken(userDetails);
            return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken));
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("RefreshToken invalido ou expirado");
        }
    }
}
