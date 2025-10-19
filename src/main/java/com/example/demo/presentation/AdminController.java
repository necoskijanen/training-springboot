package com.example.demo.presentation;

import com.example.demo.application.user.UserService;
import com.example.demo.application.user.dto.CreateUserRequest;
import com.example.demo.application.user.dto.UpdateUserRequest;
import com.example.demo.authentication.AuthenticationUtil;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.exception.UserDomainException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    /**
     * 管理者ホーム画面を表示
     */
    @GetMapping("/home")
    public String adminHome(Model model) {
        String username = authenticationUtil.getAuthenticatedUsername();
        model.addAttribute("username", username);
        return "admin/home";
    }

    @GetMapping("/batch/start")
    public String adminBatchStart() {
        return "batch/start";
    }

    @GetMapping("/batch/history")
    public String adminBatchHistory() {
        return "batch/history";
    }

    /**
     * ユーザ検索・一覧画面を表示
     */
    @GetMapping("/users")
    public String searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            Model model) {

        log.debug("Searching users: name={}, email={}", name, email);

        List<User> users = userService.searchUsers(name, email);
        model.addAttribute("users", users);
        model.addAttribute("name", name);
        model.addAttribute("email", email);

        return "admin/users/search";
    }

    /**
     * ユーザ作成フォームを表示
     */
    @GetMapping("/users/new")
    public String newUserForm(Model model) {
        model.addAttribute("createUserRequest", new CreateUserRequest());
        return "admin/users/new";
    }

    /**
     * ユーザを作成
     */
    @PostMapping("/users")
    public String createUser(
            @Valid @ModelAttribute CreateUserRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        if (result.hasErrors()) {
            log.debug("Validation errors: {}", result.getAllErrors());
            return "admin/users/new";
        }

        try {
            userService.createUser(request);
            redirectAttributes.addFlashAttribute("successMessage", "ユーザーを作成しました。");
            return "redirect:/admin/users";
        } catch (UserDomainException e) {
            log.warn("User creation failed: errorCode={}", e.getErrorCode());
            String errorMessage = messageSource.getMessage(e.getErrorCode().getMessageKey(), null, locale);
            result.reject("error.user", errorMessage);
            return "admin/users/new";
        }
    }

    /**
     * ユーザ編集フォームを表示
     */
    @GetMapping("/users/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        if (user == null) {
            log.warn("User not found: id={}", id);
            return "redirect:/admin/users";
        }

        UpdateUserRequest request = new UpdateUserRequest();
        request.setId(user.getId());
        request.setName(user.getName());
        request.setEmail(user.getEmail());
        request.setAdmin(user.getAdmin());

        model.addAttribute("updateUserRequest", request);
        return "admin/users/edit";
    }

    /**
     * ユーザを更新
     */
    @PostMapping("/users/{id}")
    public String updateUser(
            @PathVariable Long id,
            @Valid @ModelAttribute UpdateUserRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        request.setId(id);

        if (result.hasErrors()) {
            log.debug("Validation errors: {}", result.getAllErrors());
            return "admin/users/edit";
        }

        try {
            userService.updateUser(request);
            redirectAttributes.addFlashAttribute("successMessage", "ユーザーを更新しました。");
            return "redirect:/admin/users";
        } catch (UserDomainException e) {
            log.warn("User update failed: errorCode={}", e.getErrorCode());
            String errorMessage = messageSource.getMessage(e.getErrorCode().getMessageKey(), null, locale);
            result.reject("error.user", errorMessage);
            return "admin/users/edit";
        }
    }
}
