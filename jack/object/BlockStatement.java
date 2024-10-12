package object;

import java.util.ArrayList;
import java.util.List;

import object.interfaces.Statement;

public class BlockStatement implements Statement {
    public List<Statement> statements;

    public BlockStatement() {
        this.statements = new ArrayList<Statement>();
    }

    public void addStatement(Statement s) {
        this.statements.add(s);
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        for (Statement statement : this.statements) {
            out.append(statement.toString());
        }
        return out.toString();
    }

}
