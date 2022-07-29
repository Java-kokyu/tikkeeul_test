package com.hanghae0705.sbmoney.model.domain.item;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Category {
    //insert into category values(1, 'https://cdn-icons-png.flaticon.com/512/7186/7186894.png', '식료품');

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATEGORY_ID")
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String iconImg;

    @OneToMany(mappedBy = "category")
    @JsonManagedReference(value = "item-category-fk")
    private List<Item> itemList;

    public Category(Long id, String name, String iconImg, List<Item> itemList) {
        this.id = id;
        this.name = name;
        this.iconImg = iconImg;
        this.itemList = itemList;
    }
}
