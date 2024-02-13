package com.demojspapp.servlet;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.JsonParserDelegate;
import com.fasterxml.jackson.core.util.JsonParserSequence;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/users/*")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private List<User> users = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            // Get all users
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.println(users);
        } else {
            // Get a specific user
            String[] parts = pathInfo.split("/");
            if (parts.length < 2) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            int userId = Integer.parseInt(parts[1]);
            User user = getUserById(userId);
            if (user == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.println(user);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        users.add(new User(1, "First"));
        // Update an existing user
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String[] parts = pathInfo.split("/");
        if (parts.length < 2) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        int userId = Integer.parseInt(parts[1]);
        User user = getUserById(userId);
        System.out.println(user +" "+userId);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        BufferedReader reader = request.getReader();
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        reader.close();
        // Assuming request body contains JSON like: {"name": "UpdatedName"}
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> jsonMap = objectMapper.readValue(requestBody.toString(), new TypeReference<Map<String, String>>() {});
        String newName = jsonMap.get("name");
        user.setName(newName);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.println(user);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Create a new user
        String name = request.getParameter("name");
        if (name == null || name.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        int id = users.size() + 1; // Simple id assignment
        User newUser = new User(id, name);
        users.add(newUser);
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.println(newUser);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Delete an existing user
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String[] parts = pathInfo.split("/");
        if (parts.length < 2) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        int userId = Integer.parseInt(parts[1]);
        User user = getUserById(userId);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        users.remove(user);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private User getUserById(int id) {
        List<User> filteredUser = users.stream().filter(user -> user.getId() == id).collect(Collectors.toList());
        System.out.println(filteredUser);
        if(!filteredUser.isEmpty()){
            return filteredUser.get(0);
        }
        return null;
    }
}

class User {
    private int id;
    private String name;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "{\"id\": " + id + ", \"name\": \"" + name + "\"}";
    }
}
