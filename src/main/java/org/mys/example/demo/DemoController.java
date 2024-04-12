
package org.mys.example.demo;

import org.mys.example.demo.common.BaseRes;
import org.mys.example.demo.common.ResUtil;
import org.mys.example.demo.imageCode.User;
import org.mys.example.demo.util.PinyinUtil;
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
