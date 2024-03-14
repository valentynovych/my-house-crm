package com.example.myhouse24admin.specification;

import com.example.myhouse24admin.entity.InvoiceTemplate;
import org.springframework.data.jpa.domain.Specification;

public interface InvoiceTemplateSpecification {
    static Specification<InvoiceTemplate> byDefault(){
        return (root, query, builder) ->
                builder.equal(root.get("isDefault"), true);
    }
}
