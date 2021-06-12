package service;

import java.util.List;

import model.Authority;

public interface AuthorityService {
	List<Authority> findById(Long id);
	List<Authority> findByname(String name);
}
