package com.project.examBench.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.project.examBench.pojo.Exam;
import com.project.examBench.pojo.Question;
import com.project.examBench.pojo.User;
import com.project.examBench.pojo.UserExam;
import com.project.examBench.pojo.UserExamResult;
import com.project.examBench.service.ExamService;
import com.project.examBench.service.UserService;
import com.project.examBench.util.CommonUtil;
import com.project.examBench.util.SessionUtility;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ExamService examService;
	
	@Autowired
	private SessionUtility sessionUtility;
	
	@GetMapping("/register")
    public String register() {
        return "register";
    }

	@PostMapping("/register")
    public String register(final Model model, final User user) {
		Boolean registerSuccessfully = Boolean.FALSE;
		User dbUser = userService.save(user);
		
		if (dbUser != null) { //&&dbUser.getId() > 0
			registerSuccessfully = Boolean.TRUE;
		}
		model.addAttribute("registerSuccessfully", registerSuccessfully);
        return "register";
    }
	
	@PostMapping("/login")
    public String login(final Model model, final User user, final HttpSession session) {
		String returnPage = "home";
		User dbUser = userService.find(user);
		if (dbUser == null) {
			returnPage = "login";
			model.addAttribute("isUserNotExist", Boolean.TRUE);
		} else {
			sessionUtility.setIntoSession(session, CommonUtil.LOGGED_IN_USER, dbUser);
			if(dbUser.getRole()==1) {
				model.addAttribute("exams", examService.getAllExams());
				returnPage = "examList";
			}else {
				returnPage = "exam";
			}
		}
		model.addAttribute("user", dbUser);
        return returnPage;
    }
	
	@PostMapping("/updateQuestion/{id}")
	public String updateQuestion(@ModelAttribute("userDto") Question question,Model model,@PathVariable("id") Long id) {
		Map<String, Object> modelMap=model.asMap();
		examService.saveQuestion(question);
		model.addAttribute("exams", examService.getAllExams());
		String returnPage = "examList"; 
		return returnPage;
	}
	
	@PostMapping("/updateExams/{id}")
	public String updateExam(@ModelAttribute("userDto") Exam exam,Model model,@PathVariable("id") Long id) {
		Map<String, Object> modelMap=model.asMap();
		examService.saveExam(exam);
		model.addAttribute("exams", examService.getAllExams());
		String returnPage = "examList"; 
		return returnPage;
	}
	
	@GetMapping("/exams")
	public String exams(final Model model) {
		final List<UserExam> userExams = new ArrayList<>();
		userExams.add(new UserExam(1, new Exam(1L, "AAAAA", 5, 20), 1, 1));
		userExams.add(new UserExam(2, new Exam(1L, "BBBBBB", 15, 30), 0, 10));
		userExams.add(new UserExam(3, new Exam(1L, "CCCCCCC", 51, 10), 0, 101));
		userExams.add(new UserExam(4, new Exam(1L, "DDDDDDDD", 35, 40), 2, 20));
		userExams.add(new UserExam(5, new Exam(1L, "EEEEE", 25, 30), 1, 60));
		
		model.addAttribute("userExams", userExams);
		model.addAttribute("questionCountDB", 3);
		return "userExamList";
	}
	
	@GetMapping("/exams/{id}")
	public String exam(final Model model, @PathVariable("id") Long id) {
		//if id is not found in DB return an empty object
		Exam exam = examService.getExam(id);
		model.addAttribute("exam", exam);
		return "exam";
	}
	
	@GetMapping("/exams/{id}/questions")
	public String getAllQuestions(final Model model, @PathVariable("id") long id) {
		final List<Question> questions = examService.getAllQuestions(id);
		model.addAttribute("examId", id);
		model.addAttribute("questions", questions);
		return "questionList";
	}
	
	@GetMapping("/exams/{examId}/questions/{id}")
	public String getQuestion(Model model, @PathVariable("examId") long examId,@PathVariable("id") long id) {
		Question question=examService.getQuestions(examId, id);
		model.addAttribute("examId", id);
		model.addAttribute("question", question);
		return "question";
	}
	
	@PostMapping("/exams/{id}/questions")
	public String examQuestionPost(final Model model, @PathVariable("id") Long id, String examQuestions) {
		System.out.println(examQuestions);
		final List<Question> questions = new ArrayList<>();
		Arrays.asList(examQuestions.split("||*||"))
		.stream()
		.forEach(examStr -> {
			String[] elements = examStr.split("-*-");
			//questions.add(new Question(Integer.valueOf(elements[0]), elements[1], elements[2]));
		});
		//model.addAttribute("exam", new Exam(4L, "EEEEE",  5, 30));
		return "examQuestion";
	}

	@GetMapping("/userExamResult")
	public String getUserExamResult(Model model) {
		List<UserExamResult> userExamResults = new ArrayList<>();
		userExamResults.add(new UserExamResult(1, "Mohan", "English", 10, 20, 11.2));
		userExamResults.add(new UserExamResult(2, "Gohan", "Wnglish", 10, 20, 15.9));
		userExamResults.add(new UserExamResult(3, "Sohan", "Pnglish", 30, 20, 16.2));
		userExamResults.add(new UserExamResult(4, "Tohan", "Ghnglish", 10, 20, 15.8));
		userExamResults.add(new UserExamResult(5, "Pohan", "Snglish", 40, 20, 17.7));
		model.addAttribute("userExamResults", userExamResults);
		return "userExamResult";
	}
}