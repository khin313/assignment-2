package com.jdc.project.model.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jdc.project.model.dto.MemberVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;

import com.jdc.project.model.dto.Project;
import com.jdc.project.model.service.utils.ProjectHelper;
import org.springframework.util.StringUtils;

@Service
public class ProjectService {

    @Autowired
    private SimpleJdbcInsert projectInsert;

    @Autowired
    private ProjectHelper projectHelper;

    private RowMapper<Project> rowMapper;

    @Autowired
    private NamedParameterJdbcTemplate template;

    public ProjectService() {
        rowMapper = new BeanPropertyRowMapper<>(Project.class);
    }

    public int create(Project project) {
        projectHelper.validate(project);
        return projectInsert.executeAndReturnKey(projectHelper.insertParams(project)).intValue();

    }

    public Project findById(int id) {
        String sql = "select * from project where id = :id";
        return template.queryForObject(sql, Map.of("id", id), rowMapper);

    }

    public List<Project> search(String project, String manager, LocalDate dateFrom, LocalDate dateTo) {
        var sql = new StringBuffer("select * from project where 1 = 1 ");
        var param = new HashMap<String, Object>();

        if (StringUtils.hasLength(project)) {
            System.out.println(project);
            sql.append("and name like :prj");
            param.put("prj", project.concat("%"));
        }
        if (StringUtils.hasLength(manager)) {
            System.out.println(manager);
            sql.delete(0, sql.length());
            System.out.println(sql);
            sql.append("select * from project p join member m on p.manager = m.id where 1 = 1 and m.name like :manager");
            System.out.println(sql);
            param.put("manager", manager.concat("%"));
        }
        if (dateFrom != null) {
            System.out.println("from " +dateFrom);
            sql.append("and start >= :from");
            param.put("from", dateFrom);
        }
        if (dateTo != null) {
            System.out.println("to " + dateTo);
            sql.append("and start <= :to");
            param.put("to", dateTo);
        }

        return template.queryForStream(sql.toString(), param, rowMapper).toList();
    }


    public int update(int id, String name, String description, LocalDate startDate, int month) {
        String sql = "update project set name = :name, description = :description, start = :startDate, months = :months where id = :id";

        return template.update(sql, Map.of("name", name, "description", description,
                "startDate", startDate, "months", month, "id", id));
    }

    public int deleteById(int id) {
        String sql = "delete from project where id = :id";
        return template.update(sql, Map.of("id", id));
    }

}
