package com.mediolio.mvc.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mediolio.vo.HashtagVO;
import com.mediolio.vo.SubcategoryVO;

@Repository
public class ProjectDao {
	@Autowired
	private SqlSessionTemplate st;
	
	public void insertTest(){
		st.insert("proj.insertTest", 1);
	}
	
	public int selectTest(){
		return st.selectOne("proj.selectTest");
	}
	
	public List<SubcategoryVO> subcategoryList(int sc_parent){
		return st.selectList("proj.subcategoryList", sc_parent);
	}
	
	public List<HashtagVO> autocompleteTags(String h_value){
		System.out.println("입력한 값: "+h_value);
		return st.selectList("proj.autocompleteTags",h_value);
	}
}
