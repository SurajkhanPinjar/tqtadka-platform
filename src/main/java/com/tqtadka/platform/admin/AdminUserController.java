package com.tqtadka.platform.admin;

import com.tqtadka.platform.dto.AdminUserCreateRequest;
import com.tqtadka.platform.dto.AdminUserUpdateRequest;
import com.tqtadka.platform.entity.CategoryType;
import com.tqtadka.platform.entity.Role;
import com.tqtadka.platform.entity.User;
import com.tqtadka.platform.service.AdminUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    /* =======================
       LIST USERS
       ======================= */
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", adminUserService.getAllUsers());
        return "admin/users/users";
    }

    /* =======================
       CREATE USER (FORM)
       ======================= */
    @GetMapping("/create")
    public String createUserPage(Model model) {

        model.addAttribute("roles", Role.values());
        model.addAttribute("categories", CategoryType.values());
        model.addAttribute("createRequest", new AdminUserCreateRequest());

        model.addAttribute("pageTitle", "Create User");
        model.addAttribute("content", "admin/users/user-create :: content");

        return "admin/layout";
    }

    /* =======================
       CREATE USER (POST)
       ======================= */
    @PostMapping("/create")
    public String createUser(
            @ModelAttribute("createRequest") AdminUserCreateRequest request
    ) {
        adminUserService.createUser(request);
        return "redirect:/admin/users";
    }

    /* =======================
       EDIT USER (FORM)
       ======================= */
    @GetMapping("/edit/{id}")
    public String editUserPage(
            @PathVariable Long id,
            Model model
    ) {
        User user = adminUserService.getUserForEdit(id);

        // ✅ MAP ENTITY → UPDATE DTO
        AdminUserUpdateRequest updateRequest = new AdminUserUpdateRequest();
        updateRequest.setId(user.getId());
        updateRequest.setName(user.getName());
        updateRequest.setRole(user.getRole());
        updateRequest.setEnabled(user.isEnabled());
        updateRequest.setAllowedCategories(
                user.getAllowedCategories() == null
                        ? List.of()
                        : List.copyOf(user.getAllowedCategories())
        );

        model.addAttribute("updateRequest", updateRequest);
        model.addAttribute("roles", Role.values());
        model.addAttribute("categories", CategoryType.values());

        model.addAttribute("pageTitle", "Edit User");
        model.addAttribute("content", "admin/users/user-edit :: content");

        return "admin/users/user-edit";
    }

    /* =======================
       UPDATE USER (POST)
       ======================= */
    @PostMapping("/update")
    public String updateUser(
            @ModelAttribute("updateRequest") AdminUserUpdateRequest request
    ) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }

        adminUserService.updateUser(request);
        return "redirect:/admin/users";
    }

    /* =======================
       DELETE USER
       ======================= */
    @PostMapping("/delete")
    public String deleteUser(@RequestParam Long userId) {
        adminUserService.deleteUser(userId);
        return "redirect:/admin/users";
    }
}