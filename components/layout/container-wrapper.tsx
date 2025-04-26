import React, { PropsWithChildren } from "react";

import { cn } from "@/lib/utils";

export const ContainerWrapper = ({
  children,
  className,
  comp = "div",
}: PropsWithChildren<{
  className: string;
  comp?: "div" | "section" | "main";
}>) => {
  const Comp = comp;

  return (
    <Comp className={cn("container mx-auto px-6 md:px-8 lg:px-10", className)}>
      {children}
    </Comp>
  );
};
