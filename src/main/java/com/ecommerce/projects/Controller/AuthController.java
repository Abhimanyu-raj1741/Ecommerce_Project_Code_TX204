package com.ecommerce.projects.Controller;

import com.ecommerce.projects.Repositories.RoleRepository;
import com.ecommerce.projects.Repositories.UserRepository;
import com.ecommerce.projects.model.AppRole;
import com.ecommerce.projects.model.Role;
import com.ecommerce.projects.model.User;
import com.ecommerce.projects.security.jwt.JwtUtils;
import com.ecommerce.projects.security.request.LoginRequest;
import com.ecommerce.projects.security.request.SignupRequest;
import com.ecommerce.projects.security.response.MessageResponse;
import com.ecommerce.projects.security.response.UserInfoResponse;
import com.ecommerce.projects.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepository;

    @PostMapping("/siginin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest){
        Authentication authentication;
        try{
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        }
        catch (AuthenticationException exception){
            Map<String,Object> map = new HashMap<>();
            map.put("message","Bad Credentials");
            map.put("status",false);

            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwtToken=jwtUtils.generateTokenFromUsername(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(item->item.getAuthority()).collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(userDetails.getId(),userDetails.getUsername(),jwtToken,roles);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest){

        if(userRepository.existsByUserName(signupRequest.getUsername())){
                     return ResponseEntity
                             .badRequest()
                             .body(new MessageResponse("Error: Username is already taken !"));
        }
        if(userRepository.existsByEmail(signupRequest.getEmail())){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already taken !"));
        }
        User user = new User(signupRequest.getUsername(),
                signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword()));

        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();
        if(strRoles ==null){
           Role userRole =  roleRepository.findByRoleName(AppRole.ROLE_USER)
                   .orElseThrow(()->new RuntimeException("Error: Role is not found"));
           roles.add(userRole);
        }
        else {
             // admin -->ROLE_ADMIN
            //seller --> ROLE_SELLER
            strRoles.forEach(role->{
                switch (role){
                    case "admin":
                        Role adminRole=roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                        .orElseThrow(()->new RuntimeException("Error : Role is not Found"));
                        roles.add(adminRole);
                        break;
                    case "seller":
                        Role sellerRole=roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                                .orElseThrow(()->new RuntimeException("Error : Role is not Found"));
                        roles.add(sellerRole);
                        break;
                        default:
                            Role userRole =  roleRepository.findByRoleName(AppRole.ROLE_USER)
                                    .orElseThrow(()->new RuntimeException("Error: Role is not found"));
                            roles.add(userRole);
                }

            });
        }
      user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }


}
