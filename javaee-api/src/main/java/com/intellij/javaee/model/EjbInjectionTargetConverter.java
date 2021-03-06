/*
 * Copyright 2000-2007 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intellij.javaee.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.codeInsight.CodeInsightBundle;
import com.intellij.javaee.model.common.EjbReference;
import com.intellij.javaee.model.common.Resource;
import com.intellij.javaee.model.xml.InjectionTarget;
import com.intellij.javaee.model.xml.ServiceRef;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.util.PropertyMemberType;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.converters.AbstractMemberResolveConverter;
import com.intellij.util.xml.converters.AbstractMethodResolveConverter;

/**
 * @author Gregory.Shrago
 */
public class EjbInjectionTargetConverter extends AbstractMemberResolveConverter {

  @Nonnull
  protected PsiType getPsiType(final ConvertContext context) {
    final Resource resource;
    final EjbReference ejbReference;
    final ServiceRef serviceReference;
    final PsiClass aClass;
    if ((resource = DomUtil.getParentOfType(context.getInvocationElement(), Resource.class, false)) != null) {
      aClass = resource.getType().getValue();
    }
    else if ((ejbReference = DomUtil.getParentOfType(context.getInvocationElement(), EjbReference.class, false)) != null) {
      aClass = ejbReference.getBeanInterface().getValue();
    }
    else if ((serviceReference = DomUtil.getParentOfType(context.getInvocationElement(), ServiceRef.class, false)) != null) {
      aClass = serviceReference.getServiceInterface().getValue();
    }
    else {
      aClass = null;
    }
    return aClass == null? super.getPsiType(context) : JavaPsiFacade.getInstance(aClass.getProject()).getElementFactory().createType(aClass);
  }

  @Override
  protected String getPropertyName(final String s, final ConvertContext context) {
    return StringUtil.notNullize(PropertyUtil.getPropertyName(s));
  }

  protected boolean methodSuits(final PsiMethod psiMethod) {
    return AbstractMethodResolveConverter.methodSuits(psiMethod) && PropertyUtil.isSimplePropertySetter(psiMethod);
  }

  protected boolean fieldSuits(final PsiField psiField) {
    return super.fieldSuits(psiField);
  }

  @Nullable
  protected PsiClass getTargetClass(final ConvertContext context) {
    final InjectionTarget injectionTarget = context.getInvocationElement().getParentOfType(InjectionTarget.class, true);
    return injectionTarget == null? null : injectionTarget.getInjectionTargetClass().getValue();
  }

  @Nonnull
  protected PropertyMemberType[] getMemberTypes(final ConvertContext context) {
    return new PropertyMemberType[] { PropertyMemberType.SETTER, PropertyMemberType.FIELD};
  }

  @Override
  public String getErrorMessage(@Nullable final String s, final ConvertContext context) {
    return CodeInsightBundle.message("error.cannot.resolve.default.message", s);
  }
}
