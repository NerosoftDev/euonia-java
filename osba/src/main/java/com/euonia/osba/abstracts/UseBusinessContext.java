package com.euonia.osba.abstracts;

import com.euonia.osba.BusinessContext;

/**
 * Marks an object that participates in OSBA business-context propagation.
 */
public interface UseBusinessContext {
    BusinessContext getBusinessContext();

    void setBusinessContext(BusinessContext businessContext);
}
