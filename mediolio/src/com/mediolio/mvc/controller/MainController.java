package com.mediolio.mvc.model;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.mediolio.mvc.dao.HistoryDao;
import com.mediolio.mvc.dao.MainDao;
import com.mediolio.mvc.dao.ProjectDao;
import com.mediolio.vo.BranchVO;
import com.mediolio.vo.CategoryVO;
import com.mediolio.vo.HistoryVO;
import com.mediolio.vo.MemberVO;
import com.mediolio.vo.ProjectVO;

/* ***** 박성준 + 
 * ***** 모하람 + 
 * ***** 오지은 작성 class
 * */

@Controller
public class MainModel {
		
	@Autowired
	private MainDao mdao;
	@Autowired
	private HistoryDao htdao;
	@Autowired
	private ProjectDao pdao;
	
	@RequestMapping(value={"","main"})
	public ModelAndView main(HttpSession session){

		ModelAndView mav = new ModelAndView("main/index");		
		MemberVO mev = (MemberVO) session.getAttribute("mev");
		
		if(mev!=null){
			//로그인한 경우
			
			//오지은 작성 - 회원별 관심분야 최신 글
			List<CategoryVO> category = mdao.getInterestingPart(mev.getM_id());
			mav.addObject("interesting", category);
			List<ProjectVO> new1 = mdao.getNewProject_interest(category.get(0).getCate_id());
			List<ProjectVO> new2 = mdao.getNewProject_interest(category.get(1).getCate_id());

			if(new1 != null && new2 != null){
				mav.addObject("new1_idx", new1.size());
				mav.addObject("new2_idx", new2.size());
				mav.addObject("new1", new1);
				mav.addObject("new2", new2);
			}
			
			
			// 모하람 작성 - 나의 최근 히스토리
			List<BranchVO> recentHt = mdao.recentHistory(mev.getM_id());
			mav.addObject("htNum", recentHt.size());
			if(recentHt.size() != 0){
				mav.addObject("recentHtTitle",recentHt.get(0).getHistoryTitle());
				mav.addObject("recentHtBrs",recentHt);
			} 

		}else{
			//로그인하지 않은 상태 
			
			//오지은 작성 - 게임/웹&앱/영상 분야 최신 글 출력
			List<ProjectVO> new1 = mdao.getNewProject_visitor(1);
			List<ProjectVO> new2 = mdao.getNewProject_visitor(2);
			List<ProjectVO> new3 = mdao.getNewProject_visitor(3);
			
			mav.addObject("new1_idx", new1.size());
			mav.addObject("new2_idx", new2.size());
			mav.addObject("new3_idx", new3.size());
			
			mav.addObject("new1", new1);//게임
			mav.addObject("new2", new2);//웹&앱
			mav.addObject("new3", new3);//영상
			
			//모하람 작성
			mav.addObject("htNum", -1);
		}

		return mav;
	}
	
	//오지은 작성 - 메인화면의 각 카테고리 최신 글에서 "더보기"를 눌렀을 때
	@RequestMapping("mainMorePrjs")
	public String mainMorePrjs(HttpSession session, Model model, @RequestParam("cate") String cate){
		String category_name="PROJECT";
		
		if(cate.equals("1")) category_name = "GAME";
		else if(cate.equals("2")) category_name = "WEB & APP";
		else if(cate.equals("3")) category_name = "VIDEO & SOUND";
		else if(cate.equals("4")) category_name = "3D";
		else if(cate.equals("5")) category_name = "DESIGN";
		else if(cate.equals("6")) category_name = "MISC";
		
		//그 카테고리에 속한 게시물들과 카테고리 명을 들고 이동
		model.addAttribute("category_name", category_name);
		model.addAttribute("mainProjects", mdao.mainMorePrjs(Integer.parseInt(cate)));		
		return "main.selectcategory";
	}
	
	// 카테고리 메뉴 클릭 시
	// 박성준 1차 작성
	// 오지은 수정
	@RequestMapping("selcatcard")
	public String selcatcard(HttpSession session, Model model, @RequestParam("selcat") String selcat){
		String category="0";
		String category_name="PROJECT";
		List<ProjectVO> prjList;
		
		if(selcat.equals("ct_project")){
			//프로젝트인 경우
			prjList = mdao.getProjectLists();
			
		}else{
			//과제인 경우
			if(selcat.equals("ct_game")){
				category="1";
				category_name="GAME";
			}
			else if(selcat.equals("ct_webApp")){
				category="2";
				category_name="WEB & APP";
			}
			else if(selcat.equals("ct_video")){
				category="3";
				category_name="VIDEO & SOUND";
			}
			else if(selcat.equals("ct_3d")){
				category="4";
				category_name="3D";
			}
			else if(selcat.equals("ct_design")){
				category="5";
				category_name="DESIGN";
			}
			else if(selcat.equals("ct_misc")){
				category="6";
				category_name="MISC";
			}
			prjList = mdao.getCertainCategoryList(category);
			
		}
		
		model.addAttribute("category_name", category_name);
		model.addAttribute("mainProjects", prjList);
		return "main.selectcategory";
	}

	
	//모하람 작성
	// 로그인한 사용자의 히스토리, 게시물(프로젝트/과제) 목록 보여주는 마이 페이지로 이동
	@RequestMapping("gotoMyPage")
	public String gotoMyPage(Model model, HttpSession session){
		// 로그인한 사용자의 전체 히스토리 리스트
		int m_id = ((MemberVO)session.getAttribute("mev")).getM_id();
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("m_id", String.valueOf(m_id));
		map.put("type", "myPage");
		List<HistoryVO> htList = htdao.historyList(map);
		
		model.addAttribute("htList", htList);
		// 가장 최근에 업데이트 된 히스토리의 브랜치들 불러오기
		if(!htList.isEmpty()){
			HistoryVO recentHt = htList.get(0);
			model.addAttribute("recentHtId", recentHt.getHt_id());
			model.addAttribute("recentHtTitle", recentHt.getHt_title());
			
			Map<String, Object> map2 = new HashMap<String, Object>();
			map2.put("ht_id", String.valueOf(recentHt.getHt_id()));
			map2.put("type", "myPage");
			List<BranchVO> list = htdao.branchList(map2);
			model.addAttribute("branches",list);
				
		}
		
		// 사용자의 게시물
		model.addAttribute("myProjects",pdao.userProject(m_id));
		model.addAttribute("type","myPage");
		
		return "mypage/mypage";
	}
	
	
	// 박성준 작성
	@RequestMapping("selectlikepage")
	public String selectlikepage(HttpSession session, Model model){
		MemberVO mev = (MemberVO) session.getAttribute("mev");
		
		model.addAttribute("likepage",mdao.likelist(mev.getM_id()));
		return "main.selectlikepage";
	}

}
