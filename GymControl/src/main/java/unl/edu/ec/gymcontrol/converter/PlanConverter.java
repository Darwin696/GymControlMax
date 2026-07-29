package unl.edu.ec.gymcontrol.converter; // o el paquete donde tengas el de Cliente

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import unl.edu.ec.gymcontrol.domain.Plan;
import unl.edu.ec.gymcontrol.service.GymService;

@FacesConverter(value = "planConverter", managed = true)
public class PlanConverter implements Converter<Plan> {

    @Inject
    private GymService gymService;

    @Override
    public Plan getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isBlank() || value.equals("null")) {
            return null;
        }
        try {
            Long id = Long.valueOf(value);
            return gymService.buscarPlan(id);   // ← asegúrate de tener este método
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Plan plan) {
        if (plan == null || plan.getId() == null) {
            return "";
        }
        return String.valueOf(plan.getId());
    }
}