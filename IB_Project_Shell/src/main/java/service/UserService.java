package service;

import java.util.List;

import model.User;
import model.UserRequest;


public interface UserService {
    User findById(Long id);
    User findByUsername(String username);
    List<User> findAll ();
	User save(UserRequest userRequest);
}
