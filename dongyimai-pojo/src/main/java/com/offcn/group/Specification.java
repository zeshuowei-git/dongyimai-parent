package com.offcn.group;

import com.offcn.pojo.TbSpecification;
import com.offcn.pojo.TbSpecificationOption;

import java.io.Serializable;
import java.util.List;

/**
 * @author ：yz
 * @date ：Created in 2020/8/26 11:40
 * @version: 1.0
 */
public class Specification implements Serializable {

    private TbSpecification specification;
    private List<TbSpecificationOption> specificationOptionList;


    public TbSpecification getSpecification() {
        return specification;
    }

    public void setSpecification(TbSpecification specification) {
        this.specification = specification;
    }

    public List<TbSpecificationOption> getSpecificationOptionList() {
        return specificationOptionList;
    }

    public void setSpecificationOptionList(List<TbSpecificationOption> specificationOptionList) {
        this.specificationOptionList = specificationOptionList;
    }

    @Override
    public String toString() {
        return "Specification{" +
                "specification=" + specification +
                ", specificationOptionList=" + specificationOptionList +
                '}';
    }

    public Specification() {
    }
}
