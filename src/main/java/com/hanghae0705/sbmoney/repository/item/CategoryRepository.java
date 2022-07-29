package com.hanghae0705.sbmoney.repository.item;

import com.hanghae0705.sbmoney.model.domain.item.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
