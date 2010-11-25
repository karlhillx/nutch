package org.apache.nutch.api;

import java.util.List;
import java.util.Map;

import org.apache.nutch.api.JobManager.JobType;
import org.apache.nutch.api.JobStatus.State;
import org.restlet.data.Form;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

public class JobResource extends ServerResource {
  public static final String PATH = "jobs";
  public static final String DESCR = "Job manager";
  
  @Get("json")
  public Object retrieve() throws Exception {
    String cid = null;
    String jid = null;
    String cmd = null;
    Form form = getQuery();
    cid = (String)getRequestAttributes().get(Params.CRAWL_ID);
    jid = (String)getRequestAttributes().get(Params.JOB_ID);
    cmd = (String)getRequestAttributes().get(Params.CMD);
    if (form != null) {
      String v = form.getFirstValue(Params.CRAWL_ID);
      if (v != null) cid = v;
      v = form.getFirstValue(Params.JOB_ID);
      if (v != null) jid = v;
      v = form.getFirstValue(Params.CMD);
      if (v != null) cmd = v;
    }
    if (jid == null) {
      return NutchApp.jobMgr.list(cid, State.ANY);
    } else {
      // handle stop / abort / get
      if (cmd == null) {
        return NutchApp.jobMgr.get(cid, jid);
      }
      if (cmd.equals(Params.JOB_CMD_STOP)) {
        return NutchApp.jobMgr.stop(cid, jid);
      } else if (cmd.equals(Params.JOB_CMD_ABORT)) {
        return NutchApp.jobMgr.abort(cid, jid);
      } else if (cmd.equals(Params.JOB_CMD_GET)) {
        return NutchApp.jobMgr.get(cid, jid);
      } else {
        throw new Exception("Unknown command: " + cmd);
      }
    }
  }
  
  /*
   * String crawlId
   * String type
   * String confId
   * Object[] args
   */
  @Put("json")
  public Object create(Map<String,Object> args) throws Exception {
    String cid = (String)args.get(Params.CRAWL_ID);
    String typeString = (String)args.get(Params.JOB_TYPE);
    JobType type = JobType.valueOf(typeString.toUpperCase());
    String confId = (String)args.get(Params.CONF_ID);
    Map<String,Object> cmdArgs = (Map<String,Object>)args.get(Params.ARGS);
    String jobId = NutchApp.jobMgr.create(cid, type, confId, cmdArgs);
    return jobId;
  }
}
