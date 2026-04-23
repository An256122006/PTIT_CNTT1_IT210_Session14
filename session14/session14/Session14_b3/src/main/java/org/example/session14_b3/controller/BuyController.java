package org.example.session14_b3.controller;

import org.example.session14_b3.sevice.FlashSaleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BuyController {

    private FlashSaleService service = new FlashSaleService();

    @GetMapping("/buy")
    public String buy(Model model) {
        String result = service.buyNow(1L);
        model.addAttribute("Message", result);
        return "index";
    }
}