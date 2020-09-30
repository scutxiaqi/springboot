package com.example.producehelper.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SConfigMapper
{
    String findDefValueByName(@Param("varName") String varName);

    void updateValByName(@Param("varName") String varName, @Param("defValue") String defValue);
}
