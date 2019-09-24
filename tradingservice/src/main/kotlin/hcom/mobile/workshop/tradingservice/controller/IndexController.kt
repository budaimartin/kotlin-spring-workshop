package hcom.mobile.workshop.tradingservice.controller

import hcom.mobile.workshop.tradingservice.TradingUserRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class IndexController(var userRepository: TradingUserRepository) {

    @GetMapping
    fun index(model: Model): String {
        model.addAttribute("users", userRepository.findAll())
        return "index"
    }
}