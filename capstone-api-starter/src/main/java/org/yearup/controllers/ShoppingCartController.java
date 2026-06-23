package org.yearup.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;
import org.yearup.service.ShoppingCartService;
import org.yearup.service.UserService;

import java.security.Principal;

@RestController
@RequestMapping("cart")
@CrossOrigin
@PreAuthorize("isAuthenticated()") // whole controller needs login, so no point checking this on every single method
public class ShoppingCartController
{
    private final ShoppingCartService shoppingCartService;
    private final UserService userService;

    public ShoppingCartController(ShoppingCartService shoppingCartService, UserService userService)
    {
        this.shoppingCartService = shoppingCartService;
        this.userService = userService;
    }

    @GetMapping("")
    public ShoppingCart getCart(Principal principal)
    {
        String userName = principal.getName();
        User user = userService.getByUserName(userName);
        int userId = user.getId();

        return shoppingCartService.getByUserId(userId);
    }

    @PostMapping("products/{productId}")
    public ResponseEntity<ShoppingCart> addProduct(@PathVariable int productId, Principal principal)
    {
        String userName = principal.getName();
        User user = userService.getByUserName(userName);
        int userId = user.getId();

        shoppingCartService.addProduct(userId, productId);

        // doc says return the updated cart, not just some ok message,
        // so just grab the cart fresh after adding
        ShoppingCart cart = shoppingCartService.getByUserId(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(cart);
    }

    @PutMapping("products/{productId}")
    public ShoppingCart updateProduct(@PathVariable int productId, @RequestBody ShoppingCartItem item, Principal principal)
    {
        String userName = principal.getName();
        User user = userService.getByUserName(userName);
        int userId = user.getId();

        // only quantity actually matters here per the doc, ignoring the rest of the body
        shoppingCartService.updateQuantity(userId, productId, item.getQuantity());

        return shoppingCartService.getByUserId(userId);
    }

    @DeleteMapping("")
    public ShoppingCart clearCart(Principal principal)
    {
        String userName = principal.getName();
        User user = userService.getByUserName(userName);
        int userId = user.getId();

        shoppingCartService.clearCart(userId);

        // cart's empty now but still sending it back so the front end has something to refresh with
        return shoppingCartService.getByUserId(userId);
    }
}