package com.rollerhockeyfrance.manager.resources;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.rollerhockeyfrance.manager.api.Classement;
import com.rollerhockeyfrance.manager.api.Match;
import com.rollerhockeyfrance.manager.api.Scorebox;
import com.rollerhockeyfrance.manager.api.Statistique;
import com.rollerhockeyfrance.manager.core.resultat.ParseurService;
import com.yammer.metrics.annotation.Timed;

/**
 * Resource très bourine temporaire pour avoir déjà quelque chose qui fonctionne
 * pour l'Elite.
 * @author fabiensaulnier
 */
@Singleton
@Path("/elite")
@Produces(MediaType.APPLICATION_JSON)
public class EliteResource {

	@Inject ParseurService parseur;
	
	LoadingCache<String, Object> cache = CacheBuilder.newBuilder()
				.recordStats()
		       	.build(
		       			new CacheLoader<String, Object>() {
							@Override
							public Object load(String key) throws Exception {
								if(Objects.equal(key, "classement")) {
									return parseur.getClassement("2195");
								} else if(Objects.equal(key, "statistiques")) {
									return parseur.getStatistiques("2195");
								} else if(Objects.equal(key, "matchs")) {
									return parseur.getMatchs("2195", "ALL", "");
								} else if(Objects.equal(key, "scorebox")) {
									List<Match> m = parseur.getMatchs("2195", "ALL", "");
									
									Scorebox sb = new Scorebox();
									DateTime t = DateTime.now().plusDays(2);									
									for (Match match : m) {
										boolean isNext = t.isBefore(match.getDate().getTime());
										if(isNext) {
											sb.getNext().add(match);
										} else {
											sb.getLast().add(match);
										}
									}
									return sb;
								}
								return null;
							}
		       			});

	@Timed
	@GET
	@Path("/classement")
	@SuppressWarnings("unchecked")
	public Response getClassement() {
		List<Classement> classement;
		 try {
			classement = (List<Classement>) cache.get("classement");
			return Response.ok(classement).build();
		} catch (ExecutionException e) {
			e.printStackTrace();
			return Response.serverError().build();
		}
	}
	
	@Timed
	@GET
	@Path("/statistiques")
	@SuppressWarnings("unchecked")
	public Response getStatistiques() {
		List<Statistique> result;
		 try {
			result = (List<Statistique>) cache.get("statistiques");
			return Response.ok(result).build();
		} catch (ExecutionException e) {
			e.printStackTrace();
			return Response.serverError().build();
		}
	}
	
	@Timed
	@GET
	@Path("/matchs")
	@SuppressWarnings("unchecked")
	public Response getMatchs() {
		List<Match> result;
		 try {
			result = (List<Match>) cache.get("matchs");
			return Response.ok(result).build();
		} catch (ExecutionException e) {
			cache.invalidate("matchs");
			e.printStackTrace();
			return Response.serverError().build();
		}
	}
	
	@Timed
	@GET
	@Path("/scorebox")
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, "application/x-javascript; charset=UTF-8"})
	public Response getScorebox(@QueryParam("jsonp") boolean jsonp, @DefaultValue("callback") @QueryParam("callback") String callback) {		
		 try {
			Scorebox result = (Scorebox) cache.get("scorebox");
			if(jsonp) {
				JSONPObject json = new JSONPObject(callback, result);
				return Response.ok(json).build();
			} else {
				return Response.ok(result).build();
			}
		} catch (Exception e) {
			cache.invalidate("scorebox");
			e.printStackTrace();
			return Response.serverError().build();
		}
	}
	
	@GET
	@Path("/cache/refresh")
	@Produces(MediaType.TEXT_PLAIN)
	public Response refresh(@QueryParam("key") String key) {
		if(Strings.isNullOrEmpty("")) {
			cache.refresh("classement");
			cache.refresh("statistiques");
			cache.refresh("matchs");
			cache.refresh("scorebox");
		} else {
			cache.refresh(key);
		}
		return Response.ok("ok").build();
	}
	
	@GET
	@Path("/cache/stat")
	@Produces(MediaType.TEXT_PLAIN)
	public String cacheStat() {
		final CacheStats cs = cache.stats();
		return cs.toString();
	}
	
}
