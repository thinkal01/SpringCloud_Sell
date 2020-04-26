package com.note.client.i18n.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("filter")
public class FilterController {
    @Autowired
    private Response response;

    @GetMapping("/{id}")
    public Response getUser(@PathVariable Long id) {
        return response.successI18n();
    }
}
