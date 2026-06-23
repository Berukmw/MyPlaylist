package org.yearup.service;

import org.springframework.stereotype.Service;
import org.yearup.models.CartItem;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.repository.ShoppingCartRepository;

import java.util.List;

@Service
public class ShoppingCartService
{
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductService productService;

    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, ProductService productService)
    {
        this.shoppingCartRepository = shoppingCartRepository;
        this.productService = productService;
    }

    public ShoppingCart getByUserId(int userId)
    {
        ShoppingCart cart = new ShoppingCart();

        List<CartItem> cartItems = shoppingCartRepository.findByUserId(userId);

        // cart rows only have productId + quantity, gotta look up the actual
        // product separately and stick both together for the response
        for (CartItem cartItem : cartItems)
        {
            Product product = productService.getById(cartItem.getProductId());

            ShoppingCartItem item = new ShoppingCartItem();
            item.setProduct(product);
            item.setQuantity(cartItem.getQuantity());

            cart.add(item);
        }

        return cart;
    }

    public void addProduct(int userId, int productId)
    {
        CartItem existing = shoppingCartRepository.findByUserIdAndProductId(userId, productId);

        if (existing != null)
        {
            // already in there, so just bump the quantity instead of making a duplicate row
            existing.setQuantity(existing.getQuantity() + 1);
            shoppingCartRepository.save(existing);
        }
        else
        {
            CartItem newItem = new CartItem();
            newItem.setUserId(userId);
            newItem.setProductId(productId);
            newItem.setQuantity(1);
            shoppingCartRepository.save(newItem);
        }
    }

    public void updateQuantity(int userId, int productId, int quantity)
    {
        CartItem existing = shoppingCartRepository.findByUserIdAndProductId(userId, productId);

        // doc says only update if it's actually already in their cart, otherwise do nothing
        if (existing != null)
        {
            existing.setQuantity(quantity);
            shoppingCartRepository.save(existing);
        }
    }

    public void clearCart(int userId)
    {
        shoppingCartRepository.deleteByUserId(userId);
    }
}