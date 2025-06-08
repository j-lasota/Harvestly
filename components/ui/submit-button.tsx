"use client";

import type { VariantProps } from "class-variance-authority";
import { useFormStatus } from "react-dom";
import * as React from "react";

import { buttonVariants } from "@/components/ui/button";
import { cn } from "@/lib/utils";

export interface SubmitButtonProps
  extends React.ButtonHTMLAttributes<HTMLButtonElement>,
    VariantProps<typeof buttonVariants> {
  label: string;
  pendingLabel: string;
}

const SubmitButton = React.forwardRef<HTMLButtonElement, SubmitButtonProps>(
  ({ className, variant, size, label, pendingLabel, ...props }, ref) => {
    const { pending } = useFormStatus();

    return (
      <button
        className={cn(buttonVariants({ variant, size, className }))}
        ref={ref}
        aria-disabled={pending}
        type="submit"
        disabled={pending}
        aria-label="Submit"
        {...props}
      >
        {pending ? `${pendingLabel}` : `${label}`}
      </button>
    );
  }
);
SubmitButton.displayName = "SubmitButton";

export { SubmitButton, buttonVariants };
