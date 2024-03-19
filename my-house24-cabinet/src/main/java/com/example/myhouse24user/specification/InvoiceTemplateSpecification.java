package com.example.myhouse24user.specification;

import com.example.myhouse24user.entity.InvoiceTemplate;
import org.springframework.data.jpa.domain.Specification;

public interface InvoiceTemplateSpecification {
    static Specification<InvoiceTemplate> byDefault(){
        return (root, query, builder) ->
                builder.equal(root.get("isDefault"), true);
    }
}
