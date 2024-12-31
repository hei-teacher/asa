package school.hei.asa.endpoint.rest.model.th;

public record ThDailyExecutionForm(
    String date,
    String missionCode1,
    String missionPercentage1,
    String missionCode2,
    String missionPercentage2,
    String missionCode3,
    String missionPercentage3,
    String missionCode4,
    String missionPercentage4,
    String missionCode5,
    String missionPercentage5) {}
