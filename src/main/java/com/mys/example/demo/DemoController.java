
package com.mys.example.demo;

import com.mys.example.demo.common.BaseRes;
import com.mys.example.demo.common.ResUtil;
import com.mys.example.demo.pojo.User;
import com.mys.example.demo.util.PinyinUtil;
import org.springframework.web.bind.annotation.*;

/**
 *
 */
@RestController
public class DemoController {


    @PostMapping(value = "/demo")
    public BaseRes<Object> changeUser(@RequestBody User user) {
        user.setAge(18);
        String pinyinName = PinyinUtil.getFullSpell(user.getName());
        user.setName(pinyinName);
        return ResUtil.success(user);
    }
}
