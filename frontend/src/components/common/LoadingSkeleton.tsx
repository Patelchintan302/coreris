import React from 'react';

export const LoadingSkeleton: React.FC<{ rows?: number }> = ({ rows = 5 }) => {
  return (
    <div className="w-full space-y-3 p-4 animate-pulse">
      <div className="h-8 bg-slate-800 rounded-lg w-1/4 mb-4" />
      {Array.from({ length: rows }).map((_, i) => (
        <div key={i} className="flex items-center space-x-4">
          <div className="h-10 bg-slate-800/60 rounded-xl flex-1" />
          <div className="h-10 bg-slate-800/60 rounded-xl w-24" />
        </div>
      ))}
    </div>
  );
};

export const CardSkeleton: React.FC<{ count?: number }> = ({ count = 4 }) => {
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 animate-pulse">
      {Array.from({ length: count }).map((_, i) => (
        <div key={i} className="p-6 bg-slate-900/50 border border-slate-800 rounded-2xl space-y-4">
          <div className="flex justify-between items-center">
            <div className="h-4 bg-slate-800 rounded w-1/2" />
            <div className="h-8 w-8 bg-slate-800 rounded-xl" />
          </div>
          <div className="h-8 bg-slate-800 rounded w-1/3" />
        </div>
      ))}
    </div>
  );
};
