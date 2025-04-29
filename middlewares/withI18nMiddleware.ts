import { NextRequest, NextFetchEvent, NextResponse } from 'next/server';
import createIntlMiddleware from 'next-intl/middleware';
import { routing } from '../i18n/routing';
import type { CustomMiddleware } from './chain';

const intlMiddleware = createIntlMiddleware(routing);

export function withI18n(next: CustomMiddleware): CustomMiddleware {
  return async (
    request: NextRequest,
    event: NextFetchEvent,
    response: NextResponse
  ) => {
    const intlResponse = intlMiddleware(request);
    if (intlResponse) {
      return intlResponse;
    }

    return next(request, event, response);
  };
}
