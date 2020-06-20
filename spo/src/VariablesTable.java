import java.util.ArrayList;
import java.util.List;

public class VariablesTable {

    public static class  tVariable {
        String type = "";        //int
        String title = "";       //sayHello ()
        String  value = "";      // null
        String parent = "";      // указывается title родителя. Если нет - null

        List<tVariable> _listOfArguments = new ArrayList<>();   //list of arguments

        public tVariable (String type, String title, String value, String parent, List<tVariable> _listOfArguments) {
            this.title = title;
            this.type = type;
            this.value = value;
            this._listOfArguments = _listOfArguments;
        }

        public tVariable(String type, String title) {
            this.title = title;
            this.type = type;
        }

        public tVariable(String title) {
            this.title = title;
        }
    }

    List<tVariable> _listOfVariables = new ArrayList<>(); //фактический список всех групп переменных/функций

    public List<tVariable> get_listOfVariables() {
        return _listOfVariables;
    }

    public boolean addVariable(tVariable variable) {

        for (int i = 0; i < this._listOfVariables.size(); i++) {
            tVariable v = this._listOfVariables.get(i);

            if ((v.value.equals(variable.value)) &&
                    (v.title.equals(variable.title)) &&
                    (v._listOfArguments.equals(variable._listOfArguments)) &&
                    (v.parent.equals(variable.parent))) {
                return false;
            }
        }

        this._listOfVariables.add(variable);
        return true;
    }

    public boolean checkIfValueExist(tVariable variable) {

        for (int i = 0; i < this._listOfVariables.size(); i++) {
            tVariable v = this._listOfVariables.get(i);

            if (v.title.equals(variable.title)) {

                return true;
            }
        }
        return false;
    }
}
