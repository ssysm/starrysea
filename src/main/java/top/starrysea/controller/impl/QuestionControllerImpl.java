package top.starrysea.controller.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import top.starrysea.common.Condition;
import top.starrysea.common.ServiceResult;
import top.starrysea.controller.IQuestionController;
import top.starrysea.object.dto.Question;
import top.starrysea.object.view.in.QuestionForAll;
import top.starrysea.object.view.in.QuestionForAnswer;
import top.starrysea.object.view.in.QuestionForAsk;
import top.starrysea.service.IQuestionService;
import static top.starrysea.common.Const.*;
import static top.starrysea.common.ResultKey.*;

@Controller
public class QuestionControllerImpl implements IQuestionController {
	@Autowired
	private IQuestionService questionService;

	@Override
	@RequestMapping(value = "/question", method = RequestMethod.GET)
	public ModelAndView queryQuestionController(Condition condition, QuestionForAll question, Device device) {
		ModelAndView modelAndView = new ModelAndView();
		question.setQuestionStatus((short) 2);
		ServiceResult serviceResult = questionService.queryAllQuestionService(condition, question.toDTO());
		if (!serviceResult.isSuccessed()) {
			modelAndView.addObject(ERRINFO, serviceResult.getErrInfo());
			modelAndView.setViewName(device.isMobile() ? MOBILE + ERROR_VIEW : ERROR_VIEW);
			return modelAndView;
		}
		List<Question> result = serviceResult.getResult(QUESTION_LIST);
		List<top.starrysea.object.view.out.QuestionForAll> voResult = result.stream().map(Question::toVoForAll)
				.collect(Collectors.toList());
		System.out.println(voResult.get(0).getAnswer());
		modelAndView.addObject("result", voResult);
		modelAndView.addObject("serviceResult", serviceResult.getNowPage());
		modelAndView.addObject("totalPage", serviceResult.getTotalPage());
		modelAndView.setViewName(device.isMobile() ? MOBILE + "question" : "question");
		return modelAndView;
	}

	@Override
	@RequestMapping(value="/question/ajax",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> queryQuestionControllerAjax(@RequestBody QuestionForAll question) {
		Map<String, Object> theResult=new HashMap<>();
		ServiceResult serviceResult=questionService.queryAllQuestionService(question.getCondition(), question.toDTO());
		if(!serviceResult.isSuccessed()){
			theResult.put(ERRINFO, serviceResult.getErrInfo());
			return theResult;
		}
		List<Question> result=serviceResult.getResult(QUESTION_LIST);
		List<top.starrysea.object.view.out.QuestionForAll> voResult=result.stream().map(Question::toVoForAll)
				.collect(Collectors.toList());
		theResult.put("result", voResult);
		theResult.put("nowPage", serviceResult.getNowPage());
		theResult.put("totalPage", serviceResult.getTotalPage());
		return theResult;
	}

	@Override
	@RequestMapping(value="/question/ask",method=RequestMethod.POST)
	public Map<String, Object> askQuestionController(@RequestBody @Valid QuestionForAsk question, BindingResult bindingResult) {
		Map<String, Object> theResult=new HashMap<>();
		if(bindingResult.hasErrors()){
			List<String> errInfo = bindingResult.getAllErrors().stream()
					.map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
			theResult.put(ERRINFO, errInfo);
			return theResult;
		}
		ServiceResult serviceResult=questionService.askQuestionService(question.toDTO());
		if(!serviceResult.isSuccessed()){
			theResult.put(ERRINFO, serviceResult.getErrInfo());
			return theResult;
		}
		theResult.put(INFO, "提问成功！");
		return theResult;
	}

	@Override
	@RequestMapping(value="/question/answer",method=RequestMethod.POST)
	public Map<String, Object> answerQuestionController(@RequestBody @Valid QuestionForAnswer question, BindingResult bindingResult) {
		Map<String, Object> theResult=new HashMap<>();
		if(bindingResult.hasErrors()){
			List<String> errInfo = bindingResult.getAllErrors().stream()
					.map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
			theResult.put(ERRINFO, errInfo);
			return theResult;
		}
		ServiceResult serviceResult=questionService.answerQuestionService(question.toDto());
		if(!serviceResult.isSuccessed()){
			theResult.put(ERRINFO, serviceResult.getErrInfo());
			return theResult;
		}
		theResult.put(INFO, "回复成功！");
		return theResult;
	}

}
