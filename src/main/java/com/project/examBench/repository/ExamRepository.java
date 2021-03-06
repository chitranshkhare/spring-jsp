package com.project.examBench.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.project.examBench.pojo.Exam;
import com.project.examBench.pojo.Question;

@Repository
public class ExamRepository {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@SuppressWarnings("unchecked")
	public int saveExam(Exam exam) {
		int examId=-1;
		try{
			examId=(int) namedParameterJdbcTemplate.queryForObject("SELECT IFNULL(id,-1) FROM exam WHERE exam.id= "+exam.getId(),(HashMap)null ,Integer.class);
		}catch(EmptyResultDataAccessException e) {
			
		}
		String sqlInsert="INSERT INTO exam (NAME,description,question_count,duration,total_marks,passing_marks)"
				+ " VALUES (:examName, :decsription, :questionCount, :durationInMin, :totalMarks, :passingMarks)";
		
		if(examId<0) {
			namedParameterJdbcTemplate.update(sqlInsert, new BeanPropertySqlParameterSource(exam));
			String sqlSelect="SELECT IFNULL(MAX(id),-1) FROM exam";
			examId=(int) namedParameterJdbcTemplate.queryForObject(sqlSelect,(HashMap)null ,Integer.class);
		}else {
			String sqlUpdate="update exam set name=:examName,question_count=:questionCount,duration=:durationInMin,"
					+ "total_marks=:totalMarks,"
					+ "passing_marks=:passingMarks where id= :id";
			namedParameterJdbcTemplate.update(sqlUpdate, new BeanPropertySqlParameterSource(exam));
		}
		return examId;
	}
	
	@SuppressWarnings("unchecked")
	public int saveQuestion(Question question) {
		return 0;
	}
		
	public Exam getExam(long examId) {
		String sqlSelect = "select id,name,description,question_count,duration,total_marks,passing_marks from exam where exam.id = "+examId;
		@SuppressWarnings("unchecked")
		List<Exam> examList = namedParameterJdbcTemplate.query(sqlSelect, (HashMap)null, (resultSet, i) -> {
            return toExam(resultSet);
        });
		if(examList.size()>0) {
			return examList.get(0);
		}
		return null;
	}
	
	public List<Exam> getAllExams(){
		String sqlSelect = "select id,name,description,question_count,duration,total_marks,passing_marks from exam ";
		@SuppressWarnings("unchecked")
		List<Exam> examList = namedParameterJdbcTemplate.query(sqlSelect, (HashMap)null, (resultSet, i) -> {
            return toExam(resultSet);
        });
		return examList;
	}
	public Question getQuestions(long examId,long questionId){
		String sqlselect ="select id,question,keywords,marks,exam_id,description from questions where exam_id="+examId+" and id="+questionId;
		@SuppressWarnings("unchecked")
		List<Question> qList = namedParameterJdbcTemplate.query(sqlselect, (HashMap)null, (resultSet, i) -> {
			Question q=toQuestion(resultSet);
			q.setSrNo(i+1);
            return q;
        });
		if(qList.size()>0)
			return qList.get(0);
		return null;
	}
	public List<Question> getAllQuestions(long examId){
		String sqlselect ="select id,question,keywords,marks,exam_id,description from questions where exam_id="+examId;
		@SuppressWarnings("unchecked")
		List<Question> qList = namedParameterJdbcTemplate.query(sqlselect, (HashMap)null, (resultSet, i) -> {
			Question q=toQuestion(resultSet);
			q.setSrNo(i+1);
            return q;
        });
		return qList;
	}

	private Question toQuestion(ResultSet rs) throws SQLException {
		Question question = new Question();
		question.setDescription(rs.getString("description"));
		question.setId(rs.getInt("id"));
		String keyWords =rs.getString("keywords");
		String[] key=keyWords.split(",");
		question.setKeyWords(new ArrayList<String>(Arrays.asList(key)));
		question.setMaxMarks(rs.getDouble("marks"));
		question.setQuestion(rs.getString("question"));
		question.setExamCode(rs.getInt("exam_id"));
		question.setKeys(keyWords);
		return question;
	}
	
	private Exam toExam(ResultSet rs) throws SQLException {
		Exam exam=new Exam();
		exam.setId(rs.getLong("id"));
		exam.setDecsription(rs.getString("description"));
		exam.setDurationInMin(rs.getInt("duration"));
		exam.setExamName(rs.getString("name"));
		exam.setObtainedMarks(0);
		exam.setPassingMarks(rs.getDouble("passing_marks"));
		exam.setQuestionCount(rs.getInt("question_count"));
		exam.setTotalMarks(rs.getDouble("total_marks"));
		exam.setQuestions(this.getAllQuestions(exam.getId()));
		return exam;
	}
}
