package newthread.votesystem.service.impl;

import newthread.votesystem.bean.Project;
import newthread.votesystem.bean.Session;
import newthread.votesystem.bean.SessionUser;
import newthread.votesystem.mappers.ProjectMapper;
import newthread.votesystem.mappers.SessionMapper;
import newthread.votesystem.mappers.SessionUserMapper;
import newthread.votesystem.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 场次操作类
 */
@Service("sessionService")
public class SessionServiceImpl implements SessionService {

    // 场次 dao
    @Autowired
    SessionMapper sessionMapper;
    // 该场次用户权限 dao
    @Autowired
    SessionUserMapper sessionUserMapper;
    // 项目 dao
    @Autowired
    ProjectMapper projectMapper;

    /**
     * 获取所有的场次
     *
     * @return
     */
    @Override
    public List<Session> getAllSession() {
        return sessionMapper.selectAll();
    }

    /**
     * 添加场次,并返回主键
     *
     * @param session
     * @return
     */
    @Override
    public Integer addSession(Session session) {
        sessionMapper.insert(session);
        return session.getSessionId();
    }

    /**
     * 根据场次主键查询场次
     *
     * @param sessionId
     * @return
     */
    @Override
    public Session getSessionBySessionId(Integer sessionId) {
        return sessionMapper.selectByPrimaryKey(sessionId);
    }

    /**
     * 赋予评委场次投票权限
     *
     * @param sessionId
     */
    @Override
    public void giveAuthority(Integer sessionId) {
        //1. 查询场次（session）表获取评委人数
        Session session = sessionMapper.selectByPrimaryKey(sessionId);
        Integer userNumber = session.getUserNumber();
        //2. 根据场次 id 删除权限信息
        SessionUser sessionUser = new SessionUser();
        sessionUser.setSessionId(sessionId);
        sessionUserMapper.delete(sessionUser);
        //3. 访问权限表,添加权限信息（session_user）
        for (int i = 1; i <= userNumber; i++) {
            sessionUserMapper.insert(new SessionUser(sessionId, "j" + i));
        }
    }

    /**
     * 修改场次信息
     *
     * @param session
     */
    @Override
    public boolean updateSession(Session session) {

        if (sessionMapper.updateByPrimaryKey(session) == 1) {
            return true;
        } else return false;
    }

    /**
     * 根据场次 id 删除场次
     * 1. 删除场次表（session）信息
     * 2. 删除用户权限信息（session_user）
     *
     * @param sessionId
     */
    @Override
    public boolean deleteBySessionId(Integer sessionId) {
        //1. 删除该场次id场次信息
        int i = sessionMapper.deleteByPrimaryKey(sessionId);
        //2. 删除用户权限信息（session_user）,根据 sessionId
        int j = sessionUserMapper.delete(new SessionUser(sessionId, null));
        //3. 删除场次下的项目
        Project project = new Project();
        project.setSessionId(sessionId);
        int k = projectMapper.delete(project);
        //判断是否删除成功
        if(i == 1 && j == 1 && k == 1){
            return  true;
        }else return false;
    }

    /**
     * 获取场次状态:返回场次状态
     *
     * @param sessionId
     */
    @Override
    public Integer getSessionState(Integer sessionId) {
        // 获取该场次
        Session session = sessionMapper.selectByPrimaryKey(sessionId);
        return session.getSessionState();
    }
}
