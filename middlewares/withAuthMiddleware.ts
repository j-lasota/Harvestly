import { NextResponse } from 'next/server';
import { auth } from '@/auth';
import type { CustomMiddleware } from './chain';

export function withAuth(next: CustomMiddleware): CustomMiddleware {
  return async (request, event, response) => {
    const session = await auth();

    if (!session?.user) {
      const signInUrl = new URL('/api/auth/signin', request.url);
      signInUrl.searchParams.set('callbackUrl', request.url);
      return NextResponse.redirect(signInUrl);
    }

    return next(request, event, response);
  };
}
