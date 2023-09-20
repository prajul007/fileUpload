package com.computhink.cvdps.service.UserService;


import com.computhink.cvdps.constants.ApplicationConstants;
import com.computhink.cvdps.exceptions.UserValidationException;
import com.computhink.cvdps.model.Users.UserInfo;
import com.computhink.cvdps.repository.UserInfoRepository;
import com.computhink.cvdps.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl {

    @Autowired
    private UserInfoRepository repository;

    @Autowired
    UserInfoRepository userInfoRepository;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    @Autowired
    private PasswordEncoder encoder;
    public String addUser(UserInfo userInfo) throws UserValidationException {
        validateUserInfo(userInfo);
        if(userInfoRepository.findByEmail(userInfo.getEmail()).isPresent()) {
            throw new UserValidationException("Username Already Exists With this Email Id!!! Please Provide new Email Address");
        }
        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        repository.save(userInfo);
        return "User Added Successfully";
    }

    private void validateUserInfo(UserInfo user) throws UserValidationException {
        if(!StringUtils.isNotNullOrEmpty(user.getEmail())){
            throw new UserValidationException("EmailId cannot be left blank. Please provide a valid email id.");
        }
        if(StringUtils.isNotNullOrEmpty(user.getEmail())) {
            if (!user.getEmail().matches(EMAIL_REGEX)) {
                throw new UserValidationException("Invalid email ID format");
            }
        }
        if(!StringUtils.isNotNullOrEmpty(user.getName())){
            throw new UserValidationException("Name cannot be left blank. Please provide a name.");
        }
        if(!StringUtils.isNotNullOrEmpty(user.getRoles())){
            throw new UserValidationException("Roles cannot be left blank. Please provide either ROLE_USER or ROLE_ADMIN");
        }
        if(StringUtils.isNotNullOrEmpty(user.getRoles())){
            if(!user.getRoles().equals(ApplicationConstants.ROLE_USER) &&  !user.getRoles().equals(ApplicationConstants.ROLE_ADMIN)){
                throw new UserValidationException("Invalid Roles Provided. Roles can be of 2 type: ROLE_USER, ROLE_ADMIN");
            }
        }
    }
}
