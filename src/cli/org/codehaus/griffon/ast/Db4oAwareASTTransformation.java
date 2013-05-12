/* --------------------------------------------------------------------
   griffon-db4o plugin
   Copyright (C) 2012-2013 Andres Almiray

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of the
   License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this library; if not, see <http://www.gnu.org/licenses/>.
   ---------------------------------------------------------------------
*/

package org.codehaus.griffon.ast;

import griffon.plugins.db4o.DefaultDb4oProvider;
import griffon.plugins.db4o.Db4oAware;
import griffon.plugins.db4o.Db4oContributionHandler;
import griffon.plugins.db4o.Db4oProvider;
import lombok.core.handlers.Db4oAwareConstants;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SimpleMessage;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.codehaus.griffon.ast.GriffonASTUtils.*;

/**
 * Handles generation of code for the {@code @Db4oAware} annotation.
 * <p/>
 *
 * @author Andres Almiray
 */
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class Db4oAwareASTTransformation extends AbstractASTTransformation implements Db4oAwareConstants {
    private static final Logger LOG = LoggerFactory.getLogger(Db4oAwareASTTransformation.class);
    private static final ClassNode DB4O_CONTRIBUTION_HANDLER_CNODE = makeClassSafe(Db4oContributionHandler.class);
    private static final ClassNode DB4O_AWARE_CNODE = makeClassSafe(Db4oAware.class);
    private static final ClassNode DB4O_PROVIDER_CNODE = makeClassSafe(Db4oProvider.class);
    private static final ClassNode DEFAULT_DB4O_PROVIDER_CNODE = makeClassSafe(DefaultDb4oProvider.class);

    private static final String[] DELEGATING_METHODS = new String[] {
        METHOD_WITH_DB4O
    };

    static {
        Arrays.sort(DELEGATING_METHODS);
    }

    /**
     * Convenience method to see if an annotated node is {@code @Db4oAware}.
     *
     * @param node the node to check
     * @return true if the node is an event publisher
     */
    public static boolean hasDb4oAwareAnnotation(AnnotatedNode node) {
        for (AnnotationNode annotation : node.getAnnotations()) {
            if (DB4O_AWARE_CNODE.equals(annotation.getClassNode())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handles the bulk of the processing, mostly delegating to other methods.
     *
     * @param nodes  the ast nodes
     * @param source the source unit for the nodes
     */
    public void visit(ASTNode[] nodes, SourceUnit source) {
        checkNodesForAnnotationAndType(nodes[0], nodes[1]);
        addDb4oContributionIfNeeded(source, (ClassNode) nodes[1]);
    }

    public static void addDb4oContributionIfNeeded(SourceUnit source, ClassNode classNode) {
        if (needsDb4oContribution(classNode, source)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Injecting " + Db4oContributionHandler.class.getName() + " into " + classNode.getName());
            }
            apply(classNode);
        }
    }

    protected static boolean needsDb4oContribution(ClassNode declaringClass, SourceUnit sourceUnit) {
        boolean found1 = false, found2 = false, found3 = false, found4 = false;
        ClassNode consideredClass = declaringClass;
        while (consideredClass != null) {
            for (MethodNode method : consideredClass.getMethods()) {
                // just check length, MOP will match it up
                found1 = method.getName().equals(METHOD_WITH_DB4O) && method.getParameters().length == 1;
                found2 = method.getName().equals(METHOD_WITH_DB4O) && method.getParameters().length == 2;
                found3 = method.getName().equals(METHOD_SET_DB4O_PROVIDER) && method.getParameters().length == 1;
                found4 = method.getName().equals(METHOD_GET_DB4O_PROVIDER) && method.getParameters().length == 0;
                if (found1 && found2 && found3 && found4) {
                    return false;
                }
            }
            consideredClass = consideredClass.getSuperClass();
        }
        if (found1 || found2 || found3 || found4) {
            sourceUnit.getErrorCollector().addErrorAndContinue(
                new SimpleMessage("@Db4oAware cannot be processed on "
                    + declaringClass.getName()
                    + " because some but not all of methods from " + Db4oContributionHandler.class.getName() + " were declared in the current class or super classes.",
                    sourceUnit)
            );
            return false;
        }
        return true;
    }

    public static void apply(ClassNode declaringClass) {
        injectInterface(declaringClass, DB4O_CONTRIBUTION_HANDLER_CNODE);

        // add field:
        // protected Db4oProvider this$db4oProvider = DefaultDb4oProvider.instance
        FieldNode providerField = declaringClass.addField(
            DB4O_PROVIDER_FIELD_NAME,
            ACC_PRIVATE | ACC_SYNTHETIC,
            DB4O_PROVIDER_CNODE,
            defaultDb4oProviderInstance());

        // add method:
        // Db4oProvider getDb4oProvider() {
        //     return this$db4oProvider
        // }
        injectMethod(declaringClass, new MethodNode(
            METHOD_GET_DB4O_PROVIDER,
            ACC_PUBLIC,
            DB4O_PROVIDER_CNODE,
            Parameter.EMPTY_ARRAY,
            NO_EXCEPTIONS,
            returns(field(providerField))
        ));

        // add method:
        // void setDb4oProvider(Db4oProvider provider) {
        //     this$db4oProvider = provider ?: DefaultDb4oProvider.instance
        // }
        injectMethod(declaringClass, new MethodNode(
            METHOD_SET_DB4O_PROVIDER,
            ACC_PUBLIC,
            ClassHelper.VOID_TYPE,
            params(
                param(DB4O_PROVIDER_CNODE, PROVIDER)),
            NO_EXCEPTIONS,
            block(
                ifs_no_return(
                    cmp(var(PROVIDER), ConstantExpression.NULL),
                    assigns(field(providerField), defaultDb4oProviderInstance()),
                    assigns(field(providerField), var(PROVIDER))
                )
            )
        ));

        for (MethodNode method : DB4O_CONTRIBUTION_HANDLER_CNODE.getMethods()) {
            if (Arrays.binarySearch(DELEGATING_METHODS, method.getName()) < 0) continue;
            List<Expression> variables = new ArrayList<Expression>();
            Parameter[] parameters = new Parameter[method.getParameters().length];
            for (int i = 0; i < method.getParameters().length; i++) {
                Parameter p = method.getParameters()[i];
                parameters[i] = new Parameter(makeClassSafe(p.getType()), p.getName());
                parameters[i].getType().setGenericsTypes(p.getType().getGenericsTypes());
                variables.add(var(p.getName()));
            }
            ClassNode returnType = makeClassSafe(method.getReturnType());
            returnType.setGenericsTypes(method.getReturnType().getGenericsTypes());
            returnType.setGenericsPlaceHolder(method.getReturnType().isGenericsPlaceHolder());

            MethodNode newMethod = new MethodNode(
                method.getName(),
                ACC_PUBLIC,
                returnType,
                parameters,
                NO_EXCEPTIONS,
                returns(call(
                    field(providerField),
                    method.getName(),
                    args(variables)))
            );
            newMethod.setGenericsTypes(method.getGenericsTypes());
            injectMethod(declaringClass, newMethod);
        }
    }

    private static Expression defaultDb4oProviderInstance() {
        return call(DEFAULT_DB4O_PROVIDER_CNODE, "getInstance", NO_ARGS);
    }
}
