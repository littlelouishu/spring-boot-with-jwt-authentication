# spring-boot-with-jwt-authentication
a java project based on spring boot with jwt authentication

### login by the admin account.(Defined in com.example.security.user.InMemoryUserRepository.init())
```
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'
```

### login by the user account.(Defined in com.example.security.user.InMemoryUserRepository.init())
```
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"password"}'
```
### use the token get from login response to access the protected resource.
```
curl -H "Authorization: Bearer <your_token>" http://localhost:8080/api/{protected-resource}
```
