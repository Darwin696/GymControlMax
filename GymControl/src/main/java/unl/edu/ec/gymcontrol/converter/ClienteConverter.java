package unl.edu.ec.gymcontrol.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

import unl.edu.ec.gymcontrol.domain.Cliente;
import unl.edu.ec.gymcontrol.service.GymService;

/**
 * Convierte entre el id (String, lo que viaja en el HTML) y el objeto
 * Cliente completo (lo que usa la capa de negocio), para que los
 * formularios de la vista trabajen siempre con el objeto, nunca con un
 * id suelto parseado a mano en el managed bean.
 *
 * managed = true habilita inyección CDI dentro de un Converter JSF.
 */
@FacesConverter(value = "clienteConverter", managed = true)
public class ClienteConverter implements Converter<Cliente> {

    @Inject
    private GymService gymService;

    @Override
    public Cliente getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return gymService.buscarCliente(Long.valueOf(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Cliente value) {
        if (value == null || value.getId() == null) {
            return "";
        }
        return String.valueOf(value.getId());
    }
}
